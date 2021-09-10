# CitizensText
A Minecraft Spigot plugins which allows NPCs from Citizens to speak.

It is an *addon* for the [Citizens](https://www.spigotmc.org/resources/citizens.13811/) plugin, which can be downloaded for free [here](https://ci.citizensnpcs.co/job/Citizens2/).

## SpigotMC download
The plugin is published on the [SpigotMC website](https://www.spigotmc.org/resources/citizenstext.40107/).

## Documentation
You can find documentation on the [GitHub Wiki](https://github.com/SkytAsul/CitizensText/wiki).

## Developers
CitizensText is built using Apache Maven on GitHub Actions. Artifacts are published against GitHub Packages.

### Dependency
```xml
		<dependency>
			<groupId>fr.skytasul</groupId>
			<artifactId>citizenstext</artifactId>
			<version>VERSION</version>
			<scope>provided</scope>
		</dependency>
```

### API
The API includes :
* a (cancellable) event when a message is sent to a player
* a way to fetch/edit/add text instances (using `CitizensText.getInstance().getTexts()`)
* a full command framework, and a way to add your own commands to the **/text** one (using `CitizensText.getInstance().getCommand()`)
* an "options" framework which allows storing datas into text instances and modify them easily through the command framework (`CitizensText.getInstance().getOptionsRegistry()`)