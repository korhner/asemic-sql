package com.asemicanalytics.sequence;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sequence.utils.DatabaseHelper;
import com.asemicanalytics.sequence.utils.ResultRow;
import com.asemicanalytics.sequence.utils.SequenceBaseTest;
import com.asemicanalytics.sequence.utils.UserActionRow;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

class SequenceOfTwoTest extends SequenceBaseTest {
  @Test
  void testTwoValidSequences() throws SQLException, ExecutionException, InterruptedException {
    // prepare user action tables
    DatabaseHelper.createUserActionTable(TableReference.of("login"), List.of(
        new UserActionRow(1, Duration.ofSeconds(1)),
        new UserActionRow(1, Duration.ofSeconds(11))
    ));
    DatabaseHelper.createUserActionTable(TableReference.of("battle"), List.of(
        new UserActionRow(1, Duration.ofSeconds(2)),
        new UserActionRow(1, Duration.ofSeconds(12))
    ));

    // execute sequence query
    String sequenceQuery = "login >> battle";
    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
        sequenceQuery, STEP_REPOSITORY,
        TableReference.of("sequence_output"));

    // validate expected result
    assertResult(List.of(
        new ResultRow(1, Duration.ofSeconds(1), "login", 1, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofSeconds(2), "battle", 1, 2, 1, 1, 2, true),
        new ResultRow(1, Duration.ofSeconds(11), "login", 2, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofSeconds(12), "battle", 2, 2, 1, 1, 2, true)
    ));
  }

}
