architectury {
    common("forge", "fabric")
    platformSetupLoomIde()
}

val minecraftVersion = project.properties["minecraft_version"] as String

loom.accessWidenerPath.set(file("src/main/resources/biomeswevegone.accesswidener"))

sourceSets.main.get().resources.srcDir("src/main/generated/resources")

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${project.properties["fabric_loader_version"]}")

    modCompileOnly("com.github.glitchfiend:TerraBlender-common:1.21-${project.properties["terrablender_version"]}")
    modCompileOnly("corgitaco.corgilib:Corgilib-Fabric:1.20.1-${project.properties["corgilib_version"]}")
    modCompileOnly("dev.corgitaco:Oh-The-Trees-Youll-Grow-common:$minecraftVersion-${project.properties["ohthetreesyoullgrow_version"]}")
    modCompileOnly("software.bernie.geckolib:geckolib-fabric-1.21:${project.properties["geckolib_version"]}")

    modCompileOnly("mcp.mobius.waila:wthit-api:fabric-${project.properties["WTHIT"]}") { isTransitive = false }
}

