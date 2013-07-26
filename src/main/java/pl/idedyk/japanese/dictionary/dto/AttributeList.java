package pl.idedyk.japanese.dictionary.dto;

import java.util.ArrayList;
import java.util.List;

public class AttributeList {
	
	private List<Attribute> attributeList = new ArrayList<Attribute>();

	public boolean contains(AttributeType attributeType) {
		
		for (Attribute currentAttribute : attributeList) {
			
			if (currentAttribute.getAttributeType() == attributeType) {
				return true;
			}
		}
		
		return false;
	}

	public void add(int i, AttributeType attributeType) {
		
		Attribute attribute = new Attribute();
		
		attribute.setAttributeType(attributeType);
		
		attributeList.add(i, attribute);
	}

	public void add(AttributeType attributeType) {
		
		Attribute attribute = new Attribute();
		
		attribute.setAttributeType(attributeType);
		
		attributeList.add(attribute);		
	}
	
	public void addAttributeValue(AttributeType attributeType, List<String> attributeValueList) {
		
		Attribute attribute = new Attribute();
		
		attribute.setAttributeType(attributeType);
		attribute.setAttributeValue(attributeValueList);
		
		attributeList.add(attribute);
	}
	
	public void addAttributeValue(AttributeType attributeType, String attributeValue) {
		
		Attribute attribute = new Attribute();
		
		attribute.setAttributeType(attributeType);
		attribute.setSingleAttributeValue(attributeValue);
		
		attributeList.add(attribute);		
	}

	public List<Attribute> getAttributeList() {
		return attributeList;
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + ((attributeList == null) ? 0 : attributeList.hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		AttributeList other = (AttributeList) obj;
		
		if (attributeList == null) {
			if (other.attributeList != null)
				return false;
			
		} else if (!attributeList.equals(other.attributeList))
			return false;
		
		return true;
	}
}
