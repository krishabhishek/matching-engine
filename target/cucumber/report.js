$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("com/infusion/trading/matching/marketOrder.feature");
formatter.feature({
  "line": 1,
  "name": "Market order",
  "description": "",
  "id": "market-order",
  "keyword": "Feature"
});
formatter.scenario({
  "line": 3,
  "name": "A market buy order is placed",
  "description": "",
  "id": "market-order;a-market-buy-order-is-placed",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 4,
  "name": "A limit sell order exists in the order book for 700 shares at 100",
  "keyword": "Given "
});
formatter.step({
  "line": 5,
  "name": "A market buy order is placed for 600 shares",
  "keyword": "When "
});
formatter.step({
  "line": 6,
  "name": "There should be 100 shares left in the order book",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "sell order exists in the order",
      "offset": 8
    },
    {
      "val": "700",
      "offset": 48
    },
    {
      "val": "100",
      "offset": 62
    }
  ],
  "location": "MarketOrderSteps.addLimitOrderToOrderBook(String,int,int)"
});
formatter.result({
  "duration": 154288368,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "buy",
      "offset": 9
    },
    {
      "val": "600",
      "offset": 33
    }
  ],
  "location": "MarketOrderSteps.addMarketOrder(String,int)"
});
formatter.result({
  "duration": 158896,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "100",
      "offset": 16
    }
  ],
  "location": "MarketOrderSteps.doNothing(int)"
});
formatter.result({
  "duration": 3863700,
  "status": "passed"
});
});