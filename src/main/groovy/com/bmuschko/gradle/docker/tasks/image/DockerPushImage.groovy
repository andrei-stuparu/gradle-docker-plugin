/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bmuschko.gradle.docker.tasks.image

import com.bmuschko.gradle.docker.tasks.AbstractDockerRemoteApiTask
import com.bmuschko.gradle.docker.response.PushImageResponseHandler
import com.bmuschko.gradle.docker.response.ResponseHandler
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class DockerPushImage extends AbstractDockerRemoteApiTask {
    private final ResponseHandler<Void> responseHandler = new PushImageResponseHandler()

    @Input
    String imageName

    @Input
    @Optional
    String tag

    @Override
    void runRemoteCommand(dockerClient) {
        logger.quiet "Pushing image with name '${getImageName()}'."
        def pushImageCmd = dockerClient.pushImageCmd(getImageName())

        if(getTag()) {
            pushImageCmd.withTag(getTag())
        }

        if(getRegistry()) {
            Class authConfigClass = threadContextClassLoader.loadClass('com.github.dockerjava.api.model.AuthConfig')
            def authConfig = authConfigClass.newInstance()
            authConfig.serverAddress = getRegistry().url
            authConfig.username = getRegistry().username
            authConfig.password = getRegistry().password
            authConfig.email = getRegistry().email
            pushImageCmd.withAuthConfig(authConfig)
        }

        InputStream response = pushImageCmd.exec()
        responseHandler.handle(response)
    }
}
