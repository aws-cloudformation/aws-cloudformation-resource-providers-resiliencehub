# AWS::ResilienceHub::ResiliencyPolicy

Resource Type Definition for Resiliency Policy.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::ResilienceHub::ResiliencyPolicy",
    "Properties" : {
        "<a href="#policyname" title="PolicyName">PolicyName</a>" : <i>String</i>,
        "<a href="#policydescription" title="PolicyDescription">PolicyDescription</a>" : <i>String</i>,
        "<a href="#datalocationconstraint" title="DataLocationConstraint">DataLocationConstraint</a>" : <i>String</i>,
        "<a href="#tier" title="Tier">Tier</a>" : <i>String</i>,
        "<a href="#policy" title="Policy">Policy</a>" : <i><a href="policy.md">Policy</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i><a href="tags.md">Tags</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::ResilienceHub::ResiliencyPolicy
Properties:
    <a href="#policyname" title="PolicyName">PolicyName</a>: <i>String</i>
    <a href="#policydescription" title="PolicyDescription">PolicyDescription</a>: <i>String</i>
    <a href="#datalocationconstraint" title="DataLocationConstraint">DataLocationConstraint</a>: <i>String</i>
    <a href="#tier" title="Tier">Tier</a>: <i>String</i>
    <a href="#policy" title="Policy">Policy</a>: <i><a href="policy.md">Policy</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i><a href="tags.md">Tags</a></i>
</pre>

## Properties

#### PolicyName

Name of Resiliency Policy.

_Required_: Yes

_Type_: String

_Pattern_: <code>^[A-Za-z0-9][A-Za-z0-9_\-]{1,59}$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PolicyDescription

Description of Resiliency Policy.

_Required_: No

_Type_: String

_Maximum_: <code>500</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DataLocationConstraint

Data Location Constraint of the Policy.

_Required_: No

_Type_: String

_Allowed Values_: <code>AnyLocation</code> | <code>SameContinent</code> | <code>SameCountry</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tier

Resiliency Policy Tier.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>MissionCritical</code> | <code>Critical</code> | <code>Important</code> | <code>CoreServices</code> | <code>NonCritical</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Policy

_Required_: Yes

_Type_: <a href="policy.md">Policy</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: <a href="tags.md">Tags</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the PolicyArn.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### PolicyArn

Amazon Resource Name (ARN) of the Resiliency Policy.
