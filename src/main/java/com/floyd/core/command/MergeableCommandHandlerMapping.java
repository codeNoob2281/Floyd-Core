package com.floyd.core.command;

/**
 * @author floyd
 */
public interface MergeableCommandHandlerMapping extends CommandHandlerMapping {

    /**
     * Merge the given handler mapping
     * @param handlerMapping the handler mapping to merge
     */
    void merge(MergeableCommandHandlerMapping handlerMapping);
}
