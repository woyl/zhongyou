package com.zhongyou.meet.mobile.IM;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imlib.model.Conversation;

/**
 * @author luopan@centerm.com
 * @date 2020-03-16 09:35.
 */
public class ChatExtensionModule extends DefaultExtensionModule {

	@Override
	public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
		List<IPluginModule> list = super.getPluginModules(conversationType);

		IPluginModule temp = null;
		for (IPluginModule module : list) {
			if (module instanceof FilePlugin) {

				temp = module;

				break;

			}

		}
		list.remove(temp);
		list.clear();
		return list;

	}

}
