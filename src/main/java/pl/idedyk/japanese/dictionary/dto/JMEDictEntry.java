package pl.idedyk.japanese.dictionary.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JMEDictEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<String> kanji = new ArrayList<String>();

	private final List<String> kana = new ArrayList<String>();

	private final List<String> pos = new ArrayList<String>();

	private final List<String> misc = new ArrayList<String>();

	private final List<String> trans = new ArrayList<String>();
	
	private final List<String> transDet = new ArrayList<String>();

	public List<String> getKanji() {
		return kanji;
	}

	public List<String> getKana() {
		return kana;
	}

	public List<String> getPos() {
		return pos;
	}

	public List<String> getMisc() {
		return misc;
	}

	public List<String> getTrans() {
		return trans;
	}

	public List<String> getTransDet() {
		return transDet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		
		int result = 1;
		
		result = prime * result + ((kana == null) ? 0 : kana.hashCode());
		result = prime * result + ((kanji == null) ? 0 : kanji.hashCode());
		result = prime * result + ((misc == null) ? 0 : misc.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((trans == null) ? 0 : trans.hashCode());
		result = prime * result + ((transDet == null) ? 0 : transDet.hashCode());
		
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
		
		JMEDictEntry other = (JMEDictEntry) obj;
		
		if (kana == null) {
			if (other.kana != null)
				return false;
		
		} else if (!kana.equals(other.kana))
			return false;
		
		if (kanji == null) {
			if (other.kanji != null)
				return false;
		
		} else if (!kanji.equals(other.kanji))
			return false;
		
		if (misc == null) {
			if (other.misc != null)
				return false;
		
		} else if (!misc.equals(other.misc))
			return false;
		
		if (pos == null) {
			if (other.pos != null)
				return false;
		
		} else if (!pos.equals(other.pos))
			return false;
		
		if (trans == null) {
			if (other.trans != null)
				return false;
		
		} else if (!trans.equals(other.trans))
			return false;
		
		if (transDet == null) {
			if (other.transDet != null)
				return false;
		
		} else if (!transDet.equals(other.transDet))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return kanji + " - " + kana + " - " + pos + " - " + misc + " - " + trans + " - " + transDet;
	}
}
