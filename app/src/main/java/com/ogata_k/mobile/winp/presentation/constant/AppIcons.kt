package com.ogata_k.mobile.winp.presentation.constant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.graphics.vector.ImageVector

class AppIcons {
    companion object {
        val addIcon: ImageVector
            get() = Icons.Filled.Add

        val editIcon: ImageVector
            get() = Icons.Filled.Edit

        val deleteIcon: ImageVector
            get() = Icons.Filled.Delete

        val closeIcon: ImageVector
            get() = Icons.Filled.Close

        val moveBackIcon: ImageVector
            get() = Icons.AutoMirrored.Filled.ArrowBack

        val moveToIcon: ImageVector
            get() = Icons.Filled.ChevronRight

        val confirmIcon: ImageVector
            get() = Icons.Filled.Info

        val checkedIcon: ImageVector
            get() = Icons.Filled.Check

        val clockIcon: ImageVector
            get() = Icons.Filled.AccessTime

        val calendarIcon: ImageVector
            get() = Icons.Filled.DateRange

        val dropdownMenuIcon: ImageVector
            get() = Icons.Filled.MoreVert

        val editNoteIcon: ImageVector
            get() = Icons.Filled.EditNote

        val summaryIcon: ImageVector
            get() = Icons.AutoMirrored.Filled.LibraryBooks
    }
}