package com.durgasoft.slot

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun MainMenu(
    onPlay: () -> Unit,
    onTop: () -> Unit,
    onTabla: () -> Unit,
    @DrawableRes slotMachineIcon: Int = R.drawable.slot_machine_icon,
    @DrawableRes btJugar: Int = R.drawable.bt_jugar,
    @DrawableRes btTop: Int = R.drawable.bt_top,
    @DrawableRes btTabla: Int = R.drawable.bt_tabla
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = slotMachineIcon),
                contentDescription = "Logo SlotMachine",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(288.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(24.dp))

            FullWidthImageButton(btJugar, "Jugar", onPlay)
            Spacer(Modifier.height(14.dp))
            FullWidthImageButton(btTop, "Top", onTop)
            Spacer(Modifier.height(14.dp))
            FullWidthImageButton(btTabla, "Tabla", onTabla)
        }
    }
}

@Composable
private fun FullWidthImageButton(
    @DrawableRes resId: Int,
    desc: String,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = desc,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .semantics { contentDescription = desc },
        contentScale = ContentScale.FillBounds
    )
}
