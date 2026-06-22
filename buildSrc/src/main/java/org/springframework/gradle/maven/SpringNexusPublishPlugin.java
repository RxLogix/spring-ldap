package org.springframework.gradle.maven;

import io.github.gradlenexus.publishplugin.NexusPublishExtension;
import io.github.gradlenexus.publishplugin.NexusPublishPlugin;
import io.github.gradlenexus.publishplugin.NexusRepository;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.net.URI;
import java.time.Duration;

public class SpringNexusPublishPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getPlugins().apply(NexusPublishPlugin.class);
		NexusPublishExtension nexusPublishing = project.getExtensions().findByType(NexusPublishExtension.class);
		// Only register the OSSRH (Sonatype) staging repository when OSSRH credentials are
		// supplied. Without this guard the aggregate `publish` task always depends on
		// `initializeOssrhStagingRepository`, which contacts s01.oss.sonatype.org and fails
		// for local-only publishing (e.g. to an internal Nexus). This mirrors the existing
		// `ossrhUsername` guard in RootProjectPlugin.
		if (project.hasProperty("ossrhUsername")) {
			nexusPublishing.getRepositories().create("ossrh", new Action<NexusRepository>() {
				@Override
				public void execute(NexusRepository nexusRepository) {
							nexusRepository.getNexusUrl().set(URI.create("https://s01.oss.sonatype.org/service/local/"));
							nexusRepository.getSnapshotRepositoryUrl().set(URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/"));
						}
			});
		}
		nexusPublishing.getConnectTimeout().set(Duration.ofMinutes(3));
		nexusPublishing.getClientTimeout().set(Duration.ofMinutes(3));
	}
}
