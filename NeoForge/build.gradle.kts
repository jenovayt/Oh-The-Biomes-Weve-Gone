import com.hypherionmc.modpublisher.properties.ModLoader

plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    neoForge()
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
    getByName("developmentNeoForge").extendsFrom(configurations["common"])
    "shadowBundle" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

loom {
    accessWidenerPath.set(project(":Common").loom.accessWidenerPath)

    runs.create("datagen") {
        data()
        programArgs("--all", "--mod", "biomeswevegone")
        programArgs("--output", project(":Common").file("src/main/generated/resources").absolutePath)
        programArgs("--existing", project(":Common").file("src/main/resources").absolutePath)
    }
}

dependencies {
    neoForge("net.neoforged:neoforge:${project.properties["neoforge_version"]}")


    "common"(project(":Common", "namedElements")) { isTransitive = false }
    "shadowBundle"(project(":Common", "transformProductionNeoForge"))

    modRuntimeOnly("me.djtheredstoner:DevAuth-neoforge:${project.properties["devauth_version"]}")  { isTransitive = false }

    //("com.eliotlash.mclib:mclib:20")
    //forgeRuntimeLibrary("com.eliotlash.mclib:mclib:20")
    modApi("com.github.glitchfiend:TerraBlender-neoforge:1.21-${project.properties["terrablender_version"]}")
    //modApi("corgitaco.corgilib:corgilib-forge:1.20.1-${project.properties["corgilib_version"]}")
    modApi("dev.corgitaco:Oh-The-Trees-Youll-Grow-neoforge:$minecraftVersion-${project.properties["ohthetreesyoullgrow_version"]}")
    modApi("software.bernie.geckolib:geckolib-neoforge-1.21:${project.properties["geckolib_version"]}")

    modCompileOnly("mcp.mobius.waila:wthit-api:neo-${project.properties["WTHIT"]}")  { isTransitive = false }
    modRuntimeOnly("mcp.mobius.waila:wthit:neo-${project.properties["WTHIT"]}")  { isTransitive = false }
    modRuntimeOnly("lol.bai:badpackets:neo-${project.properties["badPackets"]}")  { isTransitive = false }

    modApi("com.github.glitchfiend:SereneSeasons-neoforge:1.21-10.0.0.6") { isTransitive = false}
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("net/potionstudios/biomeswevegone/neoforge/datagen/**",
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
    setLoaders(ModLoader.NEOFORGE)
    val depends = mutableListOf("terrablender", "geckolib", "corgilib", "oh-the-trees-youll-grow")
    curseDepends.required.set(depends)
    modrinthDepends.required.set(depends)
}