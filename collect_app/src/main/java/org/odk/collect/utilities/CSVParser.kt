package org.odk.collect.utilities

import com.univocity.parsers.csv.CsvParser
import com.univocity.parsers.csv.CsvParserSettings
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

/**
 * A utility class for parsing CSV data using the uniVocity CSV parser.
 */
class CSVParser {

    /**
     * Parses a CSV file from an InputStream into a list of maps where each map represents a row
     * with headers as keys.
     *
     * @param inputStream The input stream containing the CSV data.
     * @return A list of rows, where each row is represented as a map of header-value pairs.
     * @throws IllegalArgumentException if the input stream is null or empty.
     */
    fun parseCsvWithHeaders(inputStream: InputStream?): List<Map<String, String>> {
        if (inputStream == null) {
            throw IllegalArgumentException("Input stream cannot be null")
        }

        val settings = CsvParserSettings().apply {
            isHeaderExtractionEnabled = true // Extract headers from the first row
            isLineSeparatorDetectionEnabled = true // Detect line separators automatically
            format.lineSeparator = "\n".toCharArray() // Default line separator
        }

        val parser = CsvParser(settings)
        val allRows = parser.parseAll(InputStreamReader(inputStream))

        // Extract headers and map rows to key-value pairs
        val headers = parser.context.headers() ?: throw IllegalArgumentException("No headers found in CSV")
        return allRows.map { row ->
            headers.zip(row).toMap()
        }
    }

    /**
     * Parses a CSV file from an InputStream into a list of lists where each inner list represents
     * a row of raw data.
     *
     * @param inputStream The input stream containing the CSV data.
     * @return A list of rows, where each row is represented as a list of strings.
     * @throws IllegalArgumentException if the input stream is null or empty.
     */
    fun parseCsvWithoutHeaders(inputStream: InputStream?): List<List<String>> {
        if (inputStream == null) {
            throw IllegalArgumentException("Input stream cannot be null")
        }

        val settings = CsvParserSettings().apply {
            isHeaderExtractionEnabled = false // Do not extract headers
            isLineSeparatorDetectionEnabled = true // Detect line separators automatically
            format.lineSeparator = "\n".toCharArray() // Default line separator
        }

        val parser = CsvParser(settings)
        val allRows = parser.parseAll(InputStreamReader(inputStream))

        return allRows.map { row -> row.toList() }
    }

    fun parse(csvFile: File): Any {
        val inputStream = csvFile.inputStream()
        return parseCsvWithHeaders(inputStream)


    }
}