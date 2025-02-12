package bean;

import java.io.Serializable;

public class StudentCourseReport implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sno;
	private String cno;
	private String sname;
	private String cname;
	private String tno;
	private String tname;
	private double score;
	private String cdate;
	private int ccredit;

	
	public String getTno() {
		return tno;
	}

	public void setTno(String tno) {
		this.tno = tno;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getCno() {
		return cno;
	}

	public void setCno(String cno) {
		this.cno = cno;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getCdate() {
		return cdate;
	}

	public void setCdate(String cdate) {
		this.cdate = cdate;
	}

	public int getCcredit() {
		return ccredit;
	}

	public void setCcredit(int ccredit) {
		this.ccredit = ccredit;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sno == null) ? 0 : sno.hashCode());
        result = prime * result + ((cno == null) ? 0 : cno.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StudentCourseReport other = (StudentCourseReport) obj;
        if (sno == null)
        {
            if (other.sno!= null)
                return false;
        }
        else if (!sno.equals(other.sno))
            return false;
        if (cno == null)
        {
            if (other.cno != null)
                return false;
        }
        else if (!cno.equals(other.cno))
            return false;
        return true;
    }
}
