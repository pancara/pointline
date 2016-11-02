package id.hardana.entity.task;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pancara on 24/02/15.
 */
@Entity
@Table(name = "taskschedule")
public class TaskSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "taskname")
    private TaskNameEnum taskName;

    /**
     * Day of week as defined in java Calendar class.
     * SUNDAY = 1
     * MONDAY = 2
     * TUESDAY = 3
     * WEDNESDAY = 4
     * THURSDAY = 5
     * FRIDAY = 6
     * SATURDAY = 7
     * and
     * EVERY_DAY = 0
     */
    @Column(name = "dayofweek")
    private Integer dayOfWeek;

    @Column(name = "time")
    @Temporal(TemporalType.TIME)
    private Date time;

    @Column(name = "taskdata")
    private String taskData;

    @Column(name = "enabled")
    private Boolean enabled = false;

    public TaskSchedule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskNameEnum getTaskName() {
        return taskName;
    }

    public void setTaskName(TaskNameEnum taskId) {
        this.taskName = taskId;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTaskData() {
        return taskData;
    }

    public void setTaskData(String taskData) {
        this.taskData = taskData;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskSchedule that = (TaskSchedule) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
