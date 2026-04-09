package org.bukharov.task_scheduler;

public record Task(Runnable runnable, long time) {}
