package com.produban.openbus.siddhiCep;

import org.wso2.siddhi.core.query.selector.attribute.factory.OutputAttributeAggregatorFactory;
import org.wso2.siddhi.core.query.selector.attribute.handler.OutputAttributeAggregator;
import org.wso2.siddhi.query.api.definition.Attribute.Type;
import org.wso2.siddhi.query.api.extension.annotation.SiddhiExtension;

@SiddhiExtension(namespace = "agregacion", function = "mediaCondIntPorcentaje")
public class MediaCondicionadaAggregatorFactory implements OutputAttributeAggregatorFactory {

	@Override
	public OutputAttributeAggregator createAttributeAggregator(Type[] types) {
		// TODO Auto-generated method stub
		return new MediaCondicionadaAggregatorInteger();
	}

}
