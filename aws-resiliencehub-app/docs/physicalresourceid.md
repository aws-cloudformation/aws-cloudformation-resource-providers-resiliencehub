# AWS::ResilienceHub::App PhysicalResourceId

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#awsaccountid" title="AwsAccountId">AwsAccountId</a>" : <i>String</i>,
    "<a href="#awsregion" title="AwsRegion">AwsRegion</a>" : <i>String</i>,
    "<a href="#identifier" title="Identifier">Identifier</a>" : <i>String</i>,
    "<a href="#type" title="Type">Type</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#awsaccountid" title="AwsAccountId">AwsAccountId</a>: <i>String</i>
<a href="#awsregion" title="AwsRegion">AwsRegion</a>: <i>String</i>
<a href="#identifier" title="Identifier">Identifier</a>: <i>String</i>
<a href="#type" title="Type">Type</a>: <i>String</i>
</pre>

## Properties

#### AwsAccountId

_Required_: No

_Type_: String

_Pattern_: <code>^[0-9]{12}$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AwsRegion

_Required_: No

_Type_: String

_Pattern_: <code>^[a-z]{2}-((iso[a-z]{0,1}-)|(gov-)){0,1}[a-z]+-[0-9]$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Identifier

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Type

_Required_: Yes

_Type_: String

_Pattern_: <code>Arn|Native</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
