package com.ardentbot.commands.ardent

import com.ardentbot.core.ArdentRegister
import com.ardentbot.core.Flag
import com.ardentbot.core.commands.Command
import com.ardentbot.core.commands.ModuleMapping
import com.ardentbot.kotlin.apply
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

@ModuleMapping("ardent")
class Invite : Command("invite", null, null) {
    override fun onInvoke(event: GuildMessageReceivedEvent, arguments: List<String>, flags: List<Flag>, register: ArdentRegister) {
        register.sender.cmdSend("You can invite me by going to [] - thanks for using Ardent!"
                .apply("<https://ardentbot.com/invite>"), this, event)
    }
}