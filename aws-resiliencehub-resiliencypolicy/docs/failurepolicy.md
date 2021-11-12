# AWS::ResilienceHub::ResiliencyPolicy FailurePolicy

Failure Policy.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#rtoinsecs" title="RtoInSecs">RtoInSecs</a>" : <i>Integer</i>,
    "<a href="#rpoinsecs" title="RpoInSecs">RpoInSecs</a>" : <i>Integer</i>
}
</pre>

### YAML

<pre>
<a href="#rtoinsecs" title="RtoInSecs">RtoInSecs</a>: <i>Integer</i>
<a href="#rpoinsecs" title="RpoInSecs">RpoInSecs</a>: <i>Integer</i>
</pre>

## Properties

#### RtoInSecs

RTO in seconds.

_Required_: Yes

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RpoInSecs

RPO in seconds.

_Required_: Yes

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
