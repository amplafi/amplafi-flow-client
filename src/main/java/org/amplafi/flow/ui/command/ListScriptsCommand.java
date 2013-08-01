package org.amplafi.flow.ui.command;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.amplafi.flow.utils.AdminTool;

public class ListScriptsCommand extends AShellCommand {

	public ListScriptsCommand(String options) {
		super(options);
	}
	@Override
	public int execute(AdminTool adminTool) {
		Map<String, String> scriptsAvailable = adminTool.getAvailableScripts();
		Iterator<Entry<String, String>> mapItem = scriptsAvailable.entrySet()
				.iterator();
		while (mapItem.hasNext()) {
			Map.Entry<String, String> kv = mapItem.next();
			System.out.print(kv.getKey());
			if (mapItem.hasNext())
				System.out.print("\t");
		}
		System.out.println();
		return 0;
	}

}
