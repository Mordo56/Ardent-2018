package com.ardentbot.commands.admin

import com.ardentbot.commands.games.send
import com.ardentbot.core.ArdentRegister
import com.ardentbot.core.Flag
import com.ardentbot.core.commands.Command
import com.ardentbot.core.commands.ELEVATED_PERMISSIONS
import com.ardentbot.core.commands.FlagModel
import com.ardentbot.core.commands.ModuleMapping
import com.ardentbot.core.get
import com.ardentbot.kotlin.*
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException

@ModuleMapping("admin")
class Clear : Command("clear", arrayOf("cl"), 4) {
    override fun onInvoke(event: GuildMessageReceivedEvent, arguments: List<String>, flags: List<Flag>, register: ArdentRegister) {
        if (flags.isEmpty() && arguments.getOrNull(0)?.toIntOrNull() == null) {
            displayHelp(event, arguments, flags, register)
            return
        }
        val amount = flags.get("n")?.value?.toIntOrNull() ?: arguments.getOrNull(0)?.toIntOrNull() ?: 10
        if (amount !in 2..100) register.sender.cmdSend(translate("clear.invalid_num", event, register), this, event)
        else {
            val channel = flags.get("c")?.value?.toChannelId()?.toChannel(event.guild) ?: event.channel
            val user = flags.get("u")?.value?.toUserId()?.toMember(event.guild)

            var toDelete = 0
            val history = channel.iterableHistory
            val messages = history.takeWhile { message ->
                if ((message.editedTime ?: message.creationTime).plusWeeks(2).toInstant()
                                .toEpochMilli() < System.currentTimeMillis()) false
                else {
                    if (amount + 1 > toDelete && (if (user != null) message.author.id == user.user.id else true)) {
                        toDelete++
                        true
                    } else false
                }
            }
            event.message.delete().queue()
            if (messages.isEmpty()) event.channel.send(Emojis.HEAVY_MULTIPLICATION_X.cmd + translate("clear.no_found", event, register), register)
            else {
                (if (messages.size <= 2) messages[0].delete() else channel.deleteMessages(messages.without(0).take(98))).queue({
                    register.sender.cmdSend(Emojis.HEAVY_CHECK_MARK.cmd + translate("clear.response", event, register).apply(messages.size - 1, channel.asMention), this, event)
                }, { e ->
                    if (e is InsufficientPermissionException) {
                        register.sender.cmdSend(translate("clear.no_permission", event, register), this, event)
                        return@queue
                    } else e.printStackTrace()
                })
            }
        }
    }

    val example = "-u @Adam (clears 10 messages from Adam)"
    val example2 = "-c #general -n 4 (clears the last 4 messages in #general)"

    val elevated = ELEVATED_PERMISSIONS(listOf(Permission.MESSAGE_MANAGE))

    val user = FlagModel("u", "user")
    val number = FlagModel("n", "number")
    val channel = FlagModel("c", "channel")
    val default = FlagModel("d", "default")
}