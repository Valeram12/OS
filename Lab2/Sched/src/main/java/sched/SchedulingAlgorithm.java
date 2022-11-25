// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.
package sched;

import java.util.ArrayList;
import java.io.*;

public class SchedulingAlgorithm {
  private static int compTime = 0;

  public static void Run(int runtime, ArrayList<Process> processes, Results result) {
    int currentProcess = 0;
    int size = processes.size();
    int completed = 0;
    Process process;
    String resultsFile = "Summary-Processes";

    result.schedT = "Batch";
    result.schedN = "Shortest remaining time first";

    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

      currentProcess = getCurrentProcess(processes);
      process = processes.get(currentProcess);

      out.println("Process: " + currentProcess + " " + compTime +" registered... (" + process.cpu_time + " " +  process.io_blocking_time + " " + process.cpu_done + ")");

      while (compTime < runtime) {

        if (currentProcess != -1 && ((process.cpu_done == process.cpu_time && process.cpu_done > 0) || process.cpu_time == 0)) {
          process.all_time = compTime;
          completed++;
          out.println("Process: " + currentProcess + " " + compTime +" completed... (" + process.cpu_time + " " +  process.io_blocking_time + " " + process.cpu_done + ")");

          if (completed == size) {
            result.compTime = compTime;
            out.close();
            return;
          }

          currentProcess = getCurrentProcess(processes);
          if (currentProcess != -1) {
            process = processes.get(currentProcess);
            out.println("Process: " + currentProcess + " " + compTime + " registered... (" + process.cpu_time + " " + process.io_blocking_time + " " + process.cpu_done + ")");
          }
        }

        if (process.burst_time_timer == process.burst_time){
          out.println("Process: " + currentProcess + " " + compTime + " I/O blocked... (" + process.cpu_time + " " +  process.io_blocking_time + " " + process.cpu_done + ")");

          if (process.io_blocking_time > 0) {
            process.io_blocked = true;
            process.burst_time_timer = 0;
          }

          currentProcess = getCurrentProcess(processes);
          if (currentProcess != -1) {
            process = processes.get(currentProcess);
            out.println("Process: " + currentProcess + " " + compTime + " registered... (" + process.cpu_time + " " + process.io_blocking_time + " " + process.cpu_done + ")");
          }
        }
        if (currentProcess != -1) {
          process.cpu_done++;
          process.burst_time_timer++;
        }

        boolean hasArrived = increaseCompTime(processes, out);
        boolean hasUnblocked = incrIOTimer(processes, out);
        if (hasUnblocked || hasArrived){
          currentProcess = getCurrentProcess(processes);
          if (currentProcess != -1) {
            process = processes.get(currentProcess);
            out.println("Process: " + currentProcess + " " + compTime + " registered... (" + process.cpu_time + " " + process.io_blocking_time + " " + process.cpu_done + ")");
          }
        }

      }
      out.close();
    } catch (IOException e) { }
    result.compTime = compTime;
  }

  private static boolean increaseCompTime(ArrayList<Process> process, PrintStream out){
    compTime++;
    boolean arrivedProcess = false;

    for (int i = 0; i < process.size(); i++){
      if (process.get(i).arrival_time == compTime) {
        arrivedProcess = true;
        out.println("Process: " + i + " " + compTime + " has arrived... (" + process.get(i).cpu_time + " " + process.get(i).io_blocking_time + " " + process.get(i).cpu_done + ")");
      }
    }

    return arrivedProcess;
  }

  private static int getCurrentProcess(ArrayList<Process> processVector){
    int curProcess = -1;
    for (int i = 0; i < processVector.size(); i++)
      if (!processVector.get(i).io_blocked && processVector.get(i).arrival_time <= compTime && !processVector.get(i).done())
        curProcess = i;
    if (curProcess == -1)
      return curProcess;
    Process process = processVector.get(curProcess);
    for (int i = 0; i < processVector.size(); i++) {
      Process temp_process = processVector.get(i);
      if (!temp_process.io_blocked && !temp_process.done() && temp_process.remainingTime() < process.remainingTime()
              && compTime >= temp_process.arrival_time){
        curProcess = i;
      }
    }
    return curProcess;
  }


  private static boolean incrIOTimer(ArrayList<Process> processVector, PrintStream out){
    boolean finish = false;

    for (int i = 0; i < processVector.size(); i++) {
      if (processVector.get(i).io_blocked) {
        processVector.get(i).io_blocking_time_timer++;
        if (processVector.get(i).io_blocking_time_timer == processVector.get(i).io_blocking_time) {
          processVector.get(i).io_blocked = false;
          processVector.get(i).io_blocking_time_timer = 0;
          finish = true;
          out.println("Process: " + i + " " + compTime +" unblocked... (" + processVector.get(i).cpu_time + " " +  processVector.get(i).io_blocking_time + " " + processVector.get(i).cpu_done + ")");
        }
      }
    }

    return finish;
  }
}
