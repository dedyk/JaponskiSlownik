package pl.idedyk.japanese.dictionary.dto;

import java.util.ArrayList;
import java.util.List;

public class JMEDictNewNativeEntry {

	private Integer ent_seq;
	
	private List<K_Ele> k_ele = new ArrayList<JMEDictNewNativeEntry.K_Ele>();
	
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

	///

	@Override
	public String toString() {
		return "JMEDictNewNativeEntry [ent_seq=" + ent_seq + ", k_ele=" + k_ele + "]";
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
}
