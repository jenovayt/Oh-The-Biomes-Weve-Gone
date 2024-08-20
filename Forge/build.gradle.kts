import com.hypherionmc.modpublisher.properties.ModLoader

plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    forge()
}

val minecraftVersion = project.properties["minecraft_version"] as String

configurations {
    create("common")
    "common" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    create("shadowBundle")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentForge").extendsFrom(configurations["common"])
    "shadowBundle" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

loom {
    accessWidenerPath.set(project(":Common").loom.accessWidenerPath)

    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig("biomeswevegone-common.mixins.json")
        mixinConfig("biomeswevegone.mixins.json")
    }

    runs.create("datagen") {
        data()
        programArgs("--all", "--mod", "biomeswevegone")
        programArgs("--output", project(":Common").file("src/main/generated/resources").absolutePath)
        programArgs("--existing", project(":Common").file("src/main/resources").absolutePath)
    }
}

dependencies {
    forge("net.minecraftforge:forge:$minecraftVersion-${project.properties["forge_version"]}")


    "common"(project(":Common", "namedElements")) { isTransitive = false }
    "shadowBundle"(project(":Common", "transformProductionForge"))

    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-latest:${project.properties["devauth_version"]}")  { isTransitive = false }

    implementation("com.eliotlash.mclib:mclib:20")
    forgeRuntimeLibrary("com.eliotlash.mclib:mclib:20")
    modApi("com.github.glitchfiend:TerraBlender-forge:1.21-${project.properties["terrablender_version"]}")
    modApi("corgitaco.corgilib:Corgilib-Forge:1.20.1-${project.properties["corgilib_version"]}")
    modApi("dev.corgitaco:Oh-The-Trees-Youll-Grow-forge:$minecraftVersion-${project.properties["ohthetreesyoullgrow_version"]}")
    modApi("software.bernie.geckolib:geckolib-forge-1.21:${project.properties["geckolib_version"]}")

    modCompileOnly("mcp.mobius.waila:wthit-api:forge-${project.properties["WTHIT"]}")  { isTransitive = false }
    modRuntimeOnly("mcp.mobius.waila:wthit:forge-${project.properties["WTHIT"]}")  { isTransitive = false }
    modRuntimeOnly("lol.bai:badpackets:forge-${project.properties["badPackets"]}")  { isTransitive = false }

    modRuntimeOnly("maven.modrinth:cyanide:4.1.0")  { isTransitive = false }

    modApi("com.github.glitchfiend:SereneSeasons-forge:1.21-10.0.0.6") { isTransitive = false}
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("net/potionstudios/biomeswevegone/forge/datagen/**",
            "architectury.common.json", ".cache/**")
        configurations = listOf(project.configurations.getByName("shadowBundle"))
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
    }
}

publisher {
    setLoaders(ModLoader.FORGE)
    val depends = mutableListOf("terrablender", "geckolib", "corgilib", "oh-the-trees-youll-grow")
    curseDepends.required.set(depends)
    modrinthDepends.required.set(depends)
}