package com.produban.openbus.analysis;

import backtype.storm.tuple.Values;
import com.produban.openbus.util.FormatUtil;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Class Description
 */
public class DateTimeTransformation extends BaseFunction {

    public final void execute(final TridentTuple tuple, final TridentCollector collector) {

        String datetime = tuple.get(0).toString();
        collector.emit(new Values(FormatUtil.getDateInFormatTimeStamp(datetime)));
    }



}
