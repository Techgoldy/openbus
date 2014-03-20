/*
* Simple pig report to find top N users from proxy log
*
* It expects two parameters:
*
*   - input_log: the path to the proxy log to analyze
*   - N: number of users in output (only top N users will be dumped to output)
*/

-- register the jar containing our UDF
REGISTER ../java/target/openbus-analysis-0.0.1-SNAPSHOT.jar;
DEFINE Parser com.produban.openbus.analysis.udf.ProxyLogParserUDF;
DEFINE Parser com.produban.openbus.analysis.udf.ProxyLogParserUDF();

-- load raw data as text lines
logs = LOAD '$input_log' USING TextLoader AS (line: chararray);

-- user our Parser to split each raw line into a tuple of fields
parsed_logs = FOREACH  logs GENERATE FLATTEN(Parser(line));

-- group log records by user
by_user = GROUP parsed_logs BY user;

user_counts = FOREACH by_user GENERATE group, COUNT(parsed_logs) as count;

ordered_users = ORDER user_counts by count DESC;

top_users = LIMIT ordered_users (INT)'$N';

STORE  top_users INTO 'top_users';
