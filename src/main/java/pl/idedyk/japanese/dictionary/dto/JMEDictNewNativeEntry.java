package pl.idedyk.japanese.dictionary.dto;

import java.util.ArrayList;
import java.util.List;

public class JMEDictNewNativeEntry {

	private Integer ent_seq;
	
	private List<K_Ele> k_ele = new ArrayList<JMEDictNewNativeEntry.K_Ele>();
	
	private List<R_Ele> r_ele = new ArrayList<JMEDictNewNativeEntry.R_Ele>();
	
	private List<Sense> sense = new ArrayList<JMEDictNewNativeEntry.Sense>();
	
	public Integer getEnt_seq() {
		return ent_seq;
	}

	public void setEnt_seq(Integer ent_seq) {
		this.ent_seq = ent_seq;
	}
	
	public List<K_Ele> getK_ele() {
		return k_ele;
	}

	public void setK_ele(List<K_Ele> k_ele) {
		this.k_ele = k_ele;
	}
	
	public List<R_Ele> getR_ele() {
		return r_ele;
	}

	public void setR_ele(List<R_Ele> r_ele) {
		this.r_ele = r_ele;
	}	

	public List<Sense> getSense() {
		return sense;
	}

	public void setSense(List<Sense> sense) {
		this.sense = sense;
	}

	///

	@Override
	public String toString() {
		return "JMEDictNewNativeEntry [ent_seq=" + ent_seq + ", k_ele=" + k_ele + ", r_ele=" + r_ele + ", sense="
				+ sense + "]";
	}

	public static class K_Ele {
		
		private String keb;
		
		private List<String> ke_inf = new ArrayList<String>();
		
		private List<String> ke_pri = new ArrayList<String>();

		public String getKeb() {
			return keb;
		}

		public void setKeb(String keb) {
			this.keb = keb;
		}

		public List<String> getKe_inf() {
			return ke_inf;
		}

		public void setKe_inf(List<String> ke_inf) {
			this.ke_inf = ke_inf;
		}

		public List<String> getKe_pri() {
			return ke_pri;
		}

		public void setKe_pri(List<String> ke_pri) {
			this.ke_pri = ke_pri;
		}

		@Override
		public String toString() {
			return "K_Ele [keb=" + keb + ", ke_inf=" + ke_inf + ", ke_pri=" + ke_pri + "]";
		}
	}
	
	public static class R_Ele {
		
		private String reb;
		
		private boolean re_nokanji;
		
		private List<String> re_restr = new ArrayList<String>();
		
		private List<String> re_inf = new ArrayList<String>();
		
		private List<String> re_pri = new ArrayList<String>();

		public String getReb() {
			return reb;
		}

		public void setReb(String reb) {
			this.reb = reb;
		}

		public boolean isRe_nokanji() {
			return re_nokanji;
		}

		public void setRe_nokanji(boolean re_nokanji) {
			this.re_nokanji = re_nokanji;
		}

		public List<String> getRe_restr() {
			return re_restr;
		}

		public void setRe_restr(List<String> re_restr) {
			this.re_restr = re_restr;
		}

		public List<String> getRe_inf() {
			return re_inf;
		}

		public void setRe_inf(List<String> re_inf) {
			this.re_inf = re_inf;
		}

		public List<String> getRe_pri() {
			return re_pri;
		}

		public void setRe_pri(List<String> re_pri) {
			this.re_pri = re_pri;
		}

		@Override
		public String toString() {
			return "R_Ele [reb=" + reb + ", re_nokanji=" + re_nokanji + ", re_restr=" + re_restr + ", re_inf=" + re_inf
					+ ", re_pri=" + re_pri + "]";
		}
	}
	
	public static class Sense {
		
		private String pos;
		
		private List<String> stagk = new ArrayList<String>();
		
		private List<String> stagr = new ArrayList<String>();
		
		private List<String> xref = new ArrayList<String>();
		
		private List<String> ant = new ArrayList<String>();
		
		private List<String> field = new ArrayList<String>();
		
		private List<String> misc = new ArrayList<String>();
		
		private List<String> s_inf = new ArrayList<String>();
		
		private List<LSource> lsource = new ArrayList<LSource>();
		
		private List<String> dial = new ArrayList<String>();

		private List<String> gloss = new ArrayList<String>();
		
		public String getPos() {
			return pos;
		}

		public void setPos(String pos) {
			this.pos = pos;
		}

		public List<String> getGloss() {
			return gloss;
		}

		public void setGloss(List<String> gloss) {
			this.gloss = gloss;
		}

		public List<String> getXref() {
			return xref;
		}

		public void setXref(List<String> xref) {
			this.xref = xref;
		}

		public List<String> getAnt() {
			return ant;
		}

		public void setAnt(List<String> ant) {
			this.ant = ant;
		}

		public List<String> getMisc() {
			return misc;
		}

		public void setMisc(List<String> misc) {
			this.misc = misc;
		}

		public List<String> getDial() {
			return dial;
		}

		public void setDial(List<String> dial) {
			this.dial = dial;
		}

		public List<String> getStagk() {
			return stagk;
		}

		public void setStagk(List<String> stagk) {
			this.stagk = stagk;
		}

		public List<String> getStagr() {
			return stagr;
		}

		public void setStagr(List<String> stagr) {
			this.stagr = stagr;
		}

		public List<String> getS_inf() {
			return s_inf;
		}

		public void setS_inf(List<String> s_inf) {
			this.s_inf = s_inf;
		}

		public List<String> getField() {
			return field;
		}

		public void setField(List<String> field) {
			this.field = field;
		}

		public List<LSource> getLsource() {
			return lsource;
		}

		public void setLsource(List<LSource> lsource) {
			this.lsource = lsource;
		}

		@Override
		public String toString() {
			return "Sense [pos=" + pos + ", stagk=" + stagk + ", stagr=" + stagr + ", xref=" + xref + ", ant=" + ant
					+ ", field=" + field + ", misc=" + misc + ", s_inf=" + s_inf + ", lsource=" + lsource + ", dial="
					+ dial + ", gloss=" + gloss + "]";
		}
	}
	
	public static class LSource {
		
		private String lang;
		
		private String value;
		
		private String wasei;
		
		private String type;

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getWasei() {
			return wasei;
		}

		public void setWasei(String wasei) {
			this.wasei = wasei;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return "LSource [lang=" + lang + ", value=" + value + ", wasei=" + wasei + ", type=" + type + "]";
		}
	}
}
