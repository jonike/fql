package catdata.ide;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Stream;

import catdata.Util;

//TODO aql: lazy example load?

public class Examples {

	public static Map<Language, List<Example>> getExamples(File f) {
		if (!f.exists()) {
			System.err.println("File does not exist: " + f + " Warning: no built-in examples folder found.  If you are building from source, make sure words.txt is on the classpath.");
			return new HashMap<>();
		}
		Map<Language, List<Example>> es0 = new HashMap<>();
		for (Language l : Language.values()) {
			List<Example> es = new LinkedList<>();
			es0.put(l, es);
		}
		for (File dir : f.listFiles()) {
			if (dir.isHidden()) {
				continue;
			}
			for (Language l : Language.values()) {
				if (dir.getName().equals(l.fileExtension())) {
					for (File ex : dir.listFiles()) {
						if (ex.isHidden()) {
							continue;
						}
						String s = Util.readFile(ex);
						es0.get(l).add(new Example() {

							@Override
							public String getName() {
								return ex.getName().replaceAll("." + l.fileExtension(), "");
							}

							@Override
							public String getText() {
								return s;
							}

							@Override
							public Language lang() {
								return l;
							}
						});
					}
				}
			}
		}

		return es0;
	}

	private static Map<Language, List<Example>> examples2;

	public static Map<Language, List<Example>> getAllExamples2() {
		if (examples2 != null) {
			return examples2;
		}
		try {
			URL url = Object.class.getResource("/help.txt");
			if (url == null) {
				URL l = Object.class.getResource("/examples");
				if (l == null) {
					new RuntimeException("Cannot locate examples").printStackTrace();
					HashMap<Language, List<Example>> ret = new HashMap<>();
					for (Language ll : Language.values()) {
						ret.put(ll, new LinkedList<>());
					}
					
				}
				File f = new File(l.toURI());
				examples2 = getExamples(f);
				return examples2;
			} else {
				URI uri = url.toURI();
				if (uri.getScheme().equals("jar")) {
					examples2 = getExamplesFromJar(uri);
					return examples2;
				} else { //TODO AQL this is really messed up what's going on with Eclipse
					URL l = Object.class.getResource("/examples");
					if (l == null) {
						//new RuntimeException("Cannot locate built-in examples").printStackTrace();
						HashMap<Language, List<Example>> ret = new HashMap<>();
						for (Language ll : Language.values()) {
							ret.put(ll, new LinkedList<>());
						}
						return ret;
					}
					File f = new File(l.toURI());
					examples2 = getExamples(f);
					return examples2;
				}
			}
			//

		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}

	}

	private static Map<Language, List<Example>> getExamplesFromJar(URI uri) throws IOException {
		Map<Language, List<Example>> examples2 = new HashMap<>();
		for (Language l : Language.values()) {
			examples2.put(l, new LinkedList<>());
		}
		try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
			for (Language l : Language.values()) {
				List<Example> list = examples2.get(l);
				Path myPath = fileSystem.getPath("examples/" + l.fileExtension() + "/");
				try (Stream<Path> walk = Files.walk(myPath, 1)) {
					for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
						Path p = it.next();
						String s = p.toString();
						if (s.endsWith(l.fileExtension())) {
							try (InputStream in = Object.class.getResourceAsStream(s)) {
								String text = Util.readFile(in);
								
								list.add(new Example() {

									@Override
									public String getName() {
										return s.replaceAll("." + l.fileExtension(), "").replaceAll("/examples/", "");
									}

									@Override
									public String getText() {
										return text;
									}

									@Override
									public Language lang() {
										return l;
									}
								});
							}
						}
					}
				}
				examples2.put(l, list);
			}

			return examples2;
		}
	}

	private static Example[] examples;

	private static Example[] getAllExamples() {
		if (examples != null) {
			return examples;
		}

		List<Example> e = new LinkedList<>();
		for (Language l : Language.values()) {
			e.addAll(l.getExamples());
		}

		examples = e.toArray(new Example[0]);
		Arrays.sort(examples);

		return examples;
	}

	public static List<Example> getExamples(Language l) {
		return getAllExamples2().get(l);
	}

	public static Vector<Example> filterBy(String string) {
		Vector<Example> ret = new Vector<>();
		for (Example e : getAllExamples()) {
			if (e.lang().toString().equals(string)) {
				ret.add(e);
			}
		}
		return ret;
	}
}
