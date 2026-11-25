package ru.keich.mon.automation.script.version;

import com.github.difflib.text.DiffRowGenerator;

public class DiffRowGeneratorVersions {
	public final static String DIFF_CHAR_DEL = "";
	public final static String DIFF_CHAR_ADD = "";

	public static DiffRowGenerator build() {
		return DiffRowGenerator.create()
				.showInlineDiffs(true)
				.mergeOriginalRevised(false)
				.inlineDiffByWord(false)
				.oldTag(f -> DIFF_CHAR_DEL)
				.newTag(f -> DIFF_CHAR_ADD).build();
	}
}
