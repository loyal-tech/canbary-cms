package com.adopt.apigw.pojo.api;

public class TeamHierarchyDTO {
	
	private Long teamsId;

	private String TeamName;
	
	private String status;
	
	private Long parentTeamsId;
	
	public Long getTeamsId() {
		return teamsId;
	}

	public void setTeamsId(Long teamsId) {
		this.teamsId = teamsId;
	}

	public String getTeamName() {
		return TeamName;
	}

	public void setTeamName(String teamName) {
		TeamName = teamName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getParentTeamsId() {
		return parentTeamsId;
	}

	public void setParentTeamsId(Long parentTeamsId) {
		this.parentTeamsId = parentTeamsId;
	}	
}
