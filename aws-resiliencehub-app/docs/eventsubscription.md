# AWS::ResilienceHub::App EventSubscription

Indicates an event you would like to subscribe and get notification for.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#name" title="Name">Name</a>" : <i>String</i>,
    "<a href="#eventtype" title="EventType">EventType</a>" : <i>String</i>,
    "<a href="#snstopicarn" title="SnsTopicArn">SnsTopicArn</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#name" title="Name">Name</a>: <i>String</i>
<a href="#eventtype" title="EventType">EventType</a>: <i>String</i>
<a href="#snstopicarn" title="SnsTopicArn">SnsTopicArn</a>: <i>String</i>
</pre>

## Properties

#### Name

Unique name to identify an event subscription.

_Required_: Yes

_Type_: String

_Maximum Length_: <code>256</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### EventType

The type of event you would like to subscribe and get notification for.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>ScheduledAssessmentFailure</code> | <code>DriftDetected</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SnsTopicArn

Amazon Resource Name (ARN) of the Amazon Simple Notification Service topic.

_Required_: No

_Type_: String

_Pattern_: <code>^arn:(aws|aws-cn|aws-iso|aws-iso-[a-z]{1}|aws-us-gov):[A-Za-z0-9][A-Za-z0-9_/.-]{0,62}:([a-z]{2}-((iso[a-z]{0,1}-)|(gov-)){0,1}[a-z]+-[0-9]):[0-9]{12}:[A-Za-z0-9/][A-Za-z0-9:_/+.-]{0,1023}$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
