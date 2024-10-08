package com.github.unreference.refraction.domain.model;

public class AccountRanksRecord {
  private int id;
  private String accountId;
  private String rank;
  private boolean isPrimary;
  private Integer parentId;

  public AccountRanksRecord(String accountId, String rank, boolean isPrimary, Integer parentId) {
    this.accountId = accountId;
    this.rank = rank;
    this.isPrimary = isPrimary;
    this.parentId = parentId;
  }

  public AccountRanksRecord(String accountId, String rank, boolean isPrimary) {
    this.accountId = accountId;
    this.rank = rank;
    this.isPrimary = isPrimary;
    this.parentId = null;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
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
