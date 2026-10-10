/*
 * Copyright (c) 2026, Previn <https://github.com/previns>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.b0atyguide.path;

import com.b0atyguide.data.QuestHelperSteps.Instruction;
import com.b0atyguide.data.Step;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.events.PluginMessage;

/** Client-thread request state: stable guidance must not restart the external pathfinder. */
public final class AutomaticShortestPath
{
	private Step step;
	private Instruction instruction;
	private WorldPoint target;

	/** Returns one request on selection/destination changes; unchanged ticks allocate nothing. */
	public PluginMessage next(boolean enabled, Step selected, Instruction chosen)
	{
		if (!enabled)
		{
			reset();
			return null;
		}
		if (selected == step && chosen == instruction) { return null; }
		final Step previousStep = step;
		final WorldPoint previousTarget = target;
		final PluginMessage message = ShortestPathBridge.messageFor(selected, chosen);
		remember(selected, chosen, message);
		// Text/dialogue changes can select another instruction at the same place.
		return target == null || (selected == previousStep && target.equals(previousTarget))
			? null : message;
	}

	/** An explicit button request already sent this selection; do not send it again next tick. */
	public void remember(Step selected, Instruction chosen, PluginMessage message)
	{
		step = selected;
		instruction = chosen;
		target = message == null ? null : (WorldPoint) message.getData().get("target");
	}

	/** A new login/account, re-enabled integration or restarted receiver needs a fresh request. */
	public void reset()
	{
		step = null;
		instruction = null;
		target = null;
	}
}
