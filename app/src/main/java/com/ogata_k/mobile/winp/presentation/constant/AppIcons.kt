package com.ogata_k.mobile.winp.presentation.constant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

class AppIcons {
    companion object {
        val addIcon: ImageVector
            get() = Icons.Filled.Add

        val editIcon: ImageVector
            get() = Icons.Filled.Edit

        val deleteIcon: ImageVector
            get() = Icons.Filled.Delete

        val copyIcon: ImageVector
            get() = Icons.Filled.CopyAll

        val closeIcon: ImageVector
            get() = Icons.Filled.Close

        val moveBackIcon: ImageVector
            get() = Icons.AutoMirrored.Filled.ArrowBack

        val moveToIcon: ImageVector
            get() = Icons.Filled.ChevronRight

        val expandableHeaderIcon: ImageVector
            get() = Icons.Filled.ArrowDropDown

        val shrinkableHeaderIcon: ImageVector
            get() = Icons.Filled.ArrowDropUp

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

        val selectFromList: ImageVector
            get() = Icons.Filled.FilterList

        val notificationIcon: ImageVector
            get() = Icons.Filled.Notifications

        val privacyPolicyIcon: ImageVector
            get() = Icons.AutoMirrored.Filled.OpenInNew

        val licenseIcon: ImageVector
            get() = Icons.AutoMirrored.Filled.ListAlt
    }
}