# AWS::ResilienceHub::App

Resource Type Definition for AWS::ResilienceHub::App.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::ResilienceHub::App",
    "Properties" : {
        "<a href="#name" title="Name">Name</a>" : <i>String</i>,
        "<a href="#description" title="Description">Description</a>" : <i>String</i>,
        "<a href="#resiliencypolicyarn" title="ResiliencyPolicyArn">ResiliencyPolicyArn</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i><a href="tags.md">Tags</a></i>,
        "<a href="#apptemplatebody" title="AppTemplateBody">AppTemplateBody</a>" : <i>String</i>,
        "<a href="#resourcemappings" title="ResourceMappings">ResourceMappings</a>" : <i>[ <a href="resourcemapping.md">ResourceMapping</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::ResilienceHub::App
Properties:
    <a href="#name" title="Name">Name</a>: <i>String</i>
    <a href="#description" title="Description">Description</a>: <i>String</i>
    <a href="#resiliencypolicyarn" title="ResiliencyPolicyArn">ResiliencyPolicyArn</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i><a href="tags.md">Tags</a></i>
    <a href="#apptemplatebody" title="AppTemplateBody">AppTemplateBody</a>: <i>String</i>
    <a href="#resourcemappings" title="ResourceMappings">ResourceMappings</a>: <i>
      - <a href="resourcemapping.md">ResourceMapping</a></i>
</pre>

## Properties

#### Name

Name of the app.

_Required_: Yes

_Type_: String

_Pattern_: <code>^[A-Za-z0-9][A-Za-z0-9_\-]{1,59}$</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Description

App description.

_Required_: No

_Type_: String

_Maximum_: <code>500</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ResiliencyPolicyArn

Amazon Resource Name (ARN) of the Resiliency Policy.

_Required_: No

_Type_: String

_Pattern_: <code>^arn:(aws|aws-cn|aws-iso|aws-iso-[a-z]{1}|aws-us-gov):[A-Za-z0-9][A-Za-z0-9_/.-]{0,62}:([a-z]{2}-((iso[a-z]{0,1}-)|(gov-)){0,1}[a-z]+-[0-9]):[0-9]{12}:[A-Za-z0-9][A-Za-z0-9:_/+=,@.-]{0,1023}$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: <a href="tags.md">Tags</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AppTemplateBody

A string containing full ResilienceHub app template body.

_Required_: Yes

_Type_: String

_Maximum_: <code>5000</code>

_Pattern_: <code>^[\w\s:,-\.'{}\[\]:"]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ResourceMappings

An array of ResourceMapping objects.

_Required_: Yes

_Type_: List of <a href="resourcemapping.md">ResourceMapping</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the AppArn.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### AppArn

Amazon Resource Name (ARN) of the App.
