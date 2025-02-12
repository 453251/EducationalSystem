package bean;

public class Course {
	private String cno;
	private String cname;
	private String tname;
	private int chour;
	private int ccredit;
	private String cway;
	private String cdate;
	public Course() {}
	public String getCno() {
		return cno;
	}
	public void setCno(String cno) {
		this.cno = cno;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public int getCcredit() {
		return ccredit;
	}
	public void setCcredit(int credit) {
		this.ccredit = credit;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}

	public String getCway() {
		return cway;
	}
	public void setCway(String cway) {
		this.cway = cway;
	}
	public int getChour() {
		return chour;
	}
	public void setChour(int chour) {
		this.chour = chour;
	}
	public String getCdate() {
		return cdate;
	}
	public void setCdate(String cdate) {
		this.cdate = cdate;
	}

	

}
