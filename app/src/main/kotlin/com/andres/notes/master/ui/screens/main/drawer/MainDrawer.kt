package com.andres.notes.master.ui.screens.main.drawer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andres.notes.master.R
import com.andres.notes.master.ui.theme.ApplicationTheme
import com.andres.notes.master.util.getAppVersionName
import kotlinx.coroutines.launch

@Composable
fun MainDrawer(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    onTrashClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val versionName = remember { context.getAppVersionName() }

    val onItemClicked: (() -> Unit) -> Unit = { listener ->
        coroutineScope.launch {
            drawerState.close()
            listener()
        }
    }

    ModalDrawerSheet(
        modifier = modifier,
        drawerState = drawerState,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(id = R.string.my_notes_title),
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 32.dp),
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.sp
        )
        Text(
            text = versionName,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(top = 8.dp),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(26.dp))

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = Icons.Outlined.Edit,
            textRes = R.string.notes,
            selected = true,
            onClick = { onItemClicked {} }
        )

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = R.drawable.ic_delete,
            textRes = R.string.trash,
            onClick = { onItemClicked { onTrashClick() } }
        )

        DrawerItem(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = R.drawable.ic_dark_mode,
            textRes = R.string.theme,
            onClick = { onItemClicked { onThemeClick() } }
        )
    }
}

@Composable
private fun DrawerItem(
    modifier: Modifier = Modifier.padding(horizontal = 16.dp),
    textRes: Int,
    selected: Boolean = false,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    DrawerItem(
        modifier = modifier,
        textRes = textRes,
        selected = selected,
        icon = {
            Icon(imageVector = icon, contentDescription = null)
        },
        onClick = onClick
    )
}

@Composable
private fun DrawerItem(
    modifier: Modifier = Modifier,
    textRes: Int,
    selected: Boolean = false,
    icon: Int,
    onClick: () -> Unit,
) {
    DrawerItem(
        modifier = modifier,
        textRes = textRes,
        selected = selected,
        icon = {
            Icon(painter = painterResource(icon), contentDescription = null)
        },
        onClick = onClick
    )
}

@Composable
private fun DrawerItem(
    modifier: Modifier = Modifier,
    textRes: Int,
    selected: Boolean = false,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        modifier = modifier,
        label = {
            Text(
                text = stringResource(textRes),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        },
        icon = icon,
        selected = selected,
        onClick = onClick,
    )
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainDrawer(
            drawerState = DrawerState(DrawerValue.Open),
        )
    }
}