package com.asemicanalytics.core;


import com.asemicanalytics.core.column.Column;
import java.util.List;
import java.util.Optional;

public interface Dialect {
  String referenceAliasedExpression(String renderedExpression, String alias);

  String constant(String value, DataType dataType);

  String columnIdentifier(String identifier);

  String tableIdentifier(TableReference tableReference);

  String ifExpression(String condition, String ifTrue, String ifFalse);

  String truncateTimestamp(String column, TimeGrains timeGrain, int shiftDays);

  String covertToTimestamp(String column, int shiftDays);

  String intervalDays(long days);

  String epochDays(String date);

  String createTableIfNotExists(
      TableReference tableReference, List<Column> columns, Optional<Column> dateColumn);

  String addColumn(TableReference tableReference, Column column);

  default String caseExpression(String switchExpression, String whenThens, String ifFalse) {
    return "CASE " + switchExpression + " " + whenThens + " " + ifFalse + " END";
  }

  default String caseWhenThen(String when, String then) {
    return "WHEN " + when + " THEN " + then;
  }

  default String caseElse(String elseExpression) {
    return "ELSE " + elseExpression;
  }

  default String createTableFromSelect(String select, TableReference tableReference,
                                       boolean replace) {
    if (replace) {
      return "CREATE OR REPLACE TABLE " + tableIdentifier(tableReference) + " AS\n" + select;
    } else {
      return "CREATE TABLE " + tableIdentifier(tableReference) + " AS\n" + select;
    }
  }

  String epochSeconds(String timestamp);

  String matchesRegex(String expression, String regex);
}

