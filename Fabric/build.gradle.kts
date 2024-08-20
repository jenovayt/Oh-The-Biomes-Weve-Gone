import com.hypherionmc.modpublisher.properties.ModLoader

plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
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
    getByName("developmentFabric").extendsFrom(configurations["common"])
    "shadowBundle" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

loom.accessWidenerPath.set(project(":Common").loom.accessWidenerPath)

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${project.properties["fabric_loader_version"]}")
    modApi("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_api_version"]}+$minecraftVersion")

    "common"(project(":Common", "namedElements")) { isTransitive = false }
    "shadowBundle"(project(":Common", "transformProductionFabric"))

    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${project.properties["devauth_version"]}")

    modApi("com.github.glitchfiend:TerraBlender-fabric:1.21-${project.properties["terrablender_version"]}")
    modApi("corgitaco.corgilib:Corgilib-Fabric:1.20.1-${project.properties["corgilib_version"]}")
    modApi("dev.corgitaco:Oh-The-Trees-Youll-Grow-fabric:$minecraftVersion-${project.properties["ohthetreesyoullgrow_version"]}")
    modApi("software.bernie.geckolib:geckolib-fabric-1.21:${project.properties["geckolib_version"]}")

    modCompileOnly("mcp.mobius.waila:wthit-api:fabric-${project.properties["WTHIT"]}")  { isTransitive = false }
    modRuntimeOnly("mcp.mobius.waila:wthit:fabric-${project.properties["WTHIT"]}")  { isTransitive = false }
    modRuntimeOnly("lol.bai:badpackets:fabric-${project.properties["badPackets"]}")  { isTransitive = false }

    modRuntimeOnly("maven.modrinth:cyanide:jCWZy4Gl")  { isTransitive = false }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("net/potionstudios/biomeswevegone/fabric/datagen/**",
            "architectury.common.json", ".cache/**", "data/biomeswevegone/forge/**",
            "data/forge/**")
        configurations = listOf(project.configurations.getByName("shadowBundle"))
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
    }
}

publisher {
    setLoaders(ModLoader.FABRIC, ModLoader.QUILT)
    curseDepends.required.set(mutableListOf("fabric-api", "terrablender-fabric", "geckolib", "corgilib", "oh-the-trees-youll-grow"))
    modrinthDepends.required.set(mutableListOf("fabric-api", "terrablender", "geckolib", "corgilib", "oh-the-trees-youll-grow"))
}
