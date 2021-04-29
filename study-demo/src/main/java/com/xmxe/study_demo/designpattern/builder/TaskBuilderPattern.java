package com.xmxe.study_demo.designpattern.builder;

import java.util.Date;

public class TaskBuilderPattern {
    private long id;
    private String name;
    private String content;
    private int type;
    private int status;
    private Date finishDate;

    private TaskBuilderPattern(TaskBuilder taskBuilder) {
        this.id = taskBuilder.id;
        this.name = taskBuilder.name;
        this.content = taskBuilder.content;
        this.type = taskBuilder.type;
        this.status = taskBuilder.status;
        this.finishDate = taskBuilder.finishDate;
    }

    
    public static class TaskBuilder {

        private long id;
        private String name;
        private String content;
        private int type;
        private int status;
        private Date finishDate;

        public TaskBuilder(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public TaskBuilder content(String content) {
            this.content = content;
            return this;
        }

        public TaskBuilder type(int type) {
            this.type = type;
            return this;
        }

        public TaskBuilder status(int status) {
            this.status = status;
            return this;
        }

        public TaskBuilder finishDate(Date finishDate) {
            this.finishDate = finishDate;
            return this;
        }

        public TaskBuilderPattern build() {
            return new TaskBuilderPattern(this);
        }

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", name='" + name + '\'' + ", content='" + content + '\'' + ", type=" + type
                + ", status=" + status + ", finishDate=" + finishDate + '}';
    }

}
class Builder{
    TaskBuilderPattern task = new TaskBuilderPattern.TaskBuilder(99, "紧急任务")
            .type(1)
            .content("处理一下这个任务")
            .status(0)
            .finishDate(new Date())
            .build();
}
