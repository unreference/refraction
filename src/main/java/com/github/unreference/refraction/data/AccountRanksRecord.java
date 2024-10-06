package com.github.unreference.refraction.data;

import com.github.unreference.refraction.model.Rank;

public class AccountRanksRecord {
  private int id;
  private String account_id;
  private String rank;
  private boolean isPrimary;
  private Integer parentId;

  public AccountRanksRecord(String uuid, String rank, Integer parentId) {
    this.account_id = uuid;
    this.rank = rank;
    this.isPrimary = Rank.getRankFromId(rank).isPrimary();
    this.parentId = parentId;
  }

  public String getAccountId() {
    return account_id;
  }

  public void setAccount_id(String account_id) {
    this.account_id = account_id;
  }

  public String getRank() {
    return rank;
  }

  public void setRank(String rank) {
    this.rank = rank;
  }

  public boolean isPrimary() {
    return isPrimary;
  }

  public void setPrimary(boolean primary) {
    isPrimary = primary;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
