/**
 * Any copyright is dedicated to the Public Domain.
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

loadSubscript("databaseShadowing-shared.js");

async function testSteps()
{
  // The shadow database was prepared in
  // test_databaseShadowing_clearOriginsByPattern1.js

  disableNextGenLocalStorage();

  if (!importShadowDatabase("shadowdb-clearedOriginsByPattern.sqlite")) {
    return;
  }

  verifyData([4,5,6]);
}
