/*
 * Copyright (c) 2024 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.game

import java.util.concurrent.TimeUnit

object Utils {
    fun convertTimeToStringWithMls(time: Long): String {
        val min = TimeUnit.MILLISECONDS.toMinutes(time) % 60
        val sec = TimeUnit.MILLISECONDS.toSeconds(time) % 60
        val mSec = (time - TimeUnit.MILLISECONDS.toSeconds(time)) % 1000
        return String.format("%02d:%02d.%03d", min, sec, mSec)
    }
}
