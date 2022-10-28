@file:Suppress("MagicNumber")

package me.tylerbwong.stack.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import me.tylerbwong.stack.R
import me.tylerbwong.stack.api.model.User
import me.tylerbwong.stack.ui.owners.Badges
import me.tylerbwong.stack.ui.utils.compose.StackTheme
import me.tylerbwong.stack.ui.utils.format
import me.tylerbwong.stack.ui.utils.launchUrl
import me.tylerbwong.stack.ui.utils.toHtml

@Composable
fun ProfileHeader(user: User) {
    StackTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val context = LocalContext.current
            val userProfileClickableModifier = user.link?.let {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { context.launchUrl(it) },
                )
            } ?: Modifier
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://images.weserv.nl/?url=%s&output=webp".format(user.profileImage))
                    .crossfade(true)
                    .error(R.drawable.user_image_placeholder)
                    .placeholder(R.drawable.user_image_placeholder)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .width(72.dp)
                    .height(72.dp)
                    .then(userProfileClickableModifier),
            )
            user.location?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it.toHtml().toString(),
                        fontSize = 14.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.reputation.toLong().format(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                user.badgeCounts?.let {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badges(badgeCounts = it)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
