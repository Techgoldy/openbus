package com.produban.openbus.siddhiCep;

import java.text.DecimalFormat;

import org.wso2.siddhi.core.query.selector.attribute.handler.OutputAttributeAggregator;
import org.wso2.siddhi.query.api.definition.Attribute.Type;

public class MediaCondicionadaAggregatorInteger implements OutputAttributeAggregator {


	private static final long serialVersionUID = -72426648760896067L;
	private double valorAcumulado=0D;
	private double elementos=0D;
    private Double res;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public Type getReturnType() {
		return Type.DOUBLE;
	}

	@Override
	public Object processAdd(Object obj) {
		if (obj instanceof Object[]) {
            Object[] objArray = (Object[]) obj;
            String valor = (String)objArray[0];
            String match = (String)objArray[1];
            Integer percent = (Integer)objArray[2];
            elementos++;
            if(valor.equals(match)){
            	valorAcumulado+=1D;
            }
            if(elementos!=0){
    			res=valorAcumulado/elementos*percent;
    			return Integer.valueOf(res.intValue());
    		}else{
    			return Double.valueOf(0);
    		}
        }
		return Double.valueOf(0);
	}

	@Override
	public Object processRemove(Object obj) {
		if (obj instanceof Object[]) {
            Object[] objArray = (Object[]) obj;
            String valor = (String)objArray[0];
            String match = (String)objArray[1];
            elementos--;
            if(valor.equals(match)){
            	valorAcumulado-=1D;
            }
        }
		/*if(elementos<=0){
			elementos=0D;
			return Double.valueOf(0);
		}
		return Double.valueOf(valorAcumulado/elementos);*/

		return Double.valueOf(0);
	}

	@Override
	public OutputAttributeAggregator newInstance() {
		return new MediaCondicionadaAggregatorInteger();
	}

}
