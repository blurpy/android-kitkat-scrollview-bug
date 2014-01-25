
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.android;

import net.usikkert.kouchat.android.controller.TestController;
import net.usikkert.kouchat.android.util.MiscTestUtils;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;

import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests scrolling.
 *
 * @author Christian Ihle
 */
public class TestControllerTest extends ActivityInstrumentationTestCase2<TestController> {

    private Solo solo;

    public TestControllerTest() {
        super(TestController.class);
    }

    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void test06OrientationSwitchShouldScrollToBottom() {
        for (int i = 1; i <= 200; i++) {
            RobotiumTestUtils.writeLine(solo, MiscTestUtils.createLongMessage(i));

            solo.sleep(500);
            assertTrue("Line " + i + " was not visible", textIsVisible("This is message number " + i + ".9!"));
        }
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }

    private boolean textIsVisible(final String textToFind) {
        return RobotiumTestUtils.textIsVisible(solo, R.id.mainChatView, R.id.mainChatScroll, textToFind);
    }
}
