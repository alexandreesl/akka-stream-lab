package com.alexandreesl.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpecLike}

trait TestEnvironment extends WordSpecLike with Matchers with BeforeAndAfter with BeforeAndAfterAll
