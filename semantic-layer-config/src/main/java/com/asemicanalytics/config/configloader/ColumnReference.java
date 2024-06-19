package com.asemicanalytics.config.configloader;


import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.LogicalTable;

public record ColumnReference(
    LogicalTable logicalTable,
    String columnId
) {
  public Column column() {
    return logicalTable.getColumns().column(columnId);
  }
}
