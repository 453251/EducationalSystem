package bean;
/*
 * 用户实体类
 */
public class UserDB {
	private String account;	//账号
	private String password;			//密码
	private String name;
	
	public Student student;
	public Teacher teacher;
	
	// 用于在可视化信息中显示学生/教师/管理员
	private String[] flag = {"学生",
							"教师",
							"管理员"};
	
	public String[] getFlag() {
		return flag;
	}
	public void setFlag(String[] flag) {
		this.flag = flag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private Integer role;		//用户类型
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "UserDB [account=" + account + ", password=" + password + ", name=" + name + ", role=" + role + "]";
	}
	
	
}
