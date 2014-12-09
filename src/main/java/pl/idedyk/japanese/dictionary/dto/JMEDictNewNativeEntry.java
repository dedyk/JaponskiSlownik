package pl.idedyk.japanese.dictionary.dto;

import java.util.ArrayList;
import java.util.List;

public class JMEDictNewNativeEntry {

	private Integer ent_seq;
	
	private List<K_Ele> k_ele = new ArrayList<JMEDictNewNativeEntry.K_Ele>();
	
	private List<R_Ele> r_ele = new ArrayList<JMEDictNewNativeEntry.R_Ele>();
	
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

	///

	@Override
	public String toString() {
		return "JMEDictNewNativeEntry [ent_seq=" + ent_seq + ", k_ele=" + k_ele + ", r_ele=" + r_ele + "]";
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
}
