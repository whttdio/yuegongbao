package com.yuegongbao.ygb.worker.domain;

public class WorkerTrainingProgress
{
    private int completed;

    private int total;

    private boolean needComplete;

    private boolean lockAttendance;

    private boolean lockSalary;

    public int getCompleted()
    {
        return completed;
    }

    public void setCompleted(int completed)
    {
        this.completed = completed;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }

    public boolean isNeedComplete()
    {
        return needComplete;
    }

    public void setNeedComplete(boolean needComplete)
    {
        this.needComplete = needComplete;
    }

    public boolean isLockAttendance()
    {
        return lockAttendance;
    }

    public void setLockAttendance(boolean lockAttendance)
    {
        this.lockAttendance = lockAttendance;
    }

    public boolean isLockSalary()
    {
        return lockSalary;
    }

    public void setLockSalary(boolean lockSalary)
    {
        this.lockSalary = lockSalary;
    }
}
