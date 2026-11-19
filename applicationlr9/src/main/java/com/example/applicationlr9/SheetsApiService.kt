package com.example.applicationlr9

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SheetsApiService {
    @GET("v4/spreadsheets/{spreadsheetId}/values/{range}")
    suspend fun getSheetData(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Query("key") apiKey: String
    ): SheetsResponse
}
