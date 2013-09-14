package pl.idedyk.japanese.dictionary.dto;

import java.util.ArrayList;
import java.util.List;

public class Attribute {

	private AttributeType attributeType;

	private List<String> attributeValueList;

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
	}

	public List<String> getAttributeValue() {
		return attributeValueList;
	}

	public void setAttributeValue(List<String> attributeValueList) {
		this.attributeValueList = attributeValueList;
	}

	public void setSingleAttributeValue(String attributeValue) {

		attributeValueList = new ArrayList<String>();

		attributeValueList.add(attributeValue);
	}

	@Override
	public String toString() {
		return "Attribute [attributeType=" + attributeType + ", attributeValueList=" + attributeValueList + "]";
	}
}
