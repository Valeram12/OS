package sched;

public class Results {
  public String schedT;
  public String schedN;
  public int compTime;

  public Results (String schedType, String schedName, int compTime) {
    this.schedT = schedType;
    this.schedN = schedName;
    this.compTime = compTime;
  } 	
}
