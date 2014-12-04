package com.produban.openbus.siddhiCep;

import org.wso2.siddhi.core.query.selector.attribute.handler.OutputAttributeAggregator;
import org.wso2.siddhi.query.api.definition.Attribute.Type;

public class SumadorCondicionalConReinicioAggregatorLong implements OutputAttributeAggregator {

	private static final long serialVersionUID = -1590558006202301712L;
	private Long acum=0L;
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Type getReturnType() {
		return Type.LONG;
	}

	@Override
	public Object processAdd(Object obj) {
		if (obj instanceof Object[]) {
            Object[] objArray = (Object[]) obj;
            Long sumaActual = (Long)objArray[0];
            String valor = (String)objArray[1];
            String valorReinicio = (String)objArray[2];
            if(valor.equals(valorReinicio)){
            	acum=0L;
            	return 0;
            }else{
            	acum+=sumaActual;
            	return Long.valueOf(acum);
            }
        }
		return null;
	}

	@Override
	public Object processRemove(Object obj) {

           return Long.valueOf(acum);
            
	}

	@Override
	public OutputAttributeAggregator newInstance() {
		 return new SumadorCondicionalConReinicioAggregatorLong();
	}

}
