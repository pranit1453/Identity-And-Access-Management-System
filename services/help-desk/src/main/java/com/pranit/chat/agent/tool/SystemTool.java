package com.pranit.chat.agent.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public final class SystemTool {

    @Tool(
            name = "get_current_date",
            description = """
                    Returns the current local calendar date from the server in ISO-8601 format (yyyy-MM-dd).
                    Use this tool whenever you need today's date for calculations, comparisons,
                    validation, or answering questions about the current date.
                    Do not guess or infer the current date from the conversation or model knowledge.
                    """
    )
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    @Tool(
            name = "get_current_time",
            description = """
                    Returns the current local date and time in Indian Standard Time (IST)
                    in ISO-8601 format (yyyy-MM-dd'T'HH:mm:ssXXX).
                    Use this tool whenever you need the current time in India.
                    Do not guess or infer the current time from the conversation or model knowledge.
                    """
    )
    public OffsetDateTime getCurrentTime() {
        return OffsetDateTime.now(ZoneId.of("Asia/Kolkata"));
    }
}
